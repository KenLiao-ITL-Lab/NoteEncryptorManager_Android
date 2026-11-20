#!/usr/bin/env python3
"""Antigravity PreToolUse security guard for NoteEncryptorManager_Android.

Enforces project-level restrictions on sensitive files and dangerous commands:
- Returns "deny" for sensitive files and destructive/bypass commands.
- Returns "force_ask" for high-risk release, git push, and sensitive adb commands.
- Returns "ask" when a command requests BypassSandbox or a file path escapes the workspace,
  ensuring this hook never weakens Antigravity's built-in sandbox/workspace boundaries.
"""

import json
import os
import re
import sys

FILE_PATH_ARG_KEYS = (
    "Path",
    "AbsolutePath",
    "TargetFile",
    "SearchPath",
    "DirectoryPath",
    "FilePath",
)

SENSITIVE_FILE_PATTERNS = [
    re.compile(r"(^|/)local\.properties$", re.IGNORECASE),
    re.compile(r"(^|/)(keystore|signing)\.properties$", re.IGNORECASE),
    re.compile(r"(^|/)\.env(\..+)?$", re.IGNORECASE),
    re.compile(r"\.(jks|keystore|p12|pfx|pem|key)$", re.IGNORECASE),
    re.compile(r"(^|/)google-services\.json$", re.IGNORECASE),
    re.compile(r"(^|/)app/release(/|$)", re.IGNORECASE),
    re.compile(r"(^|/)\.git(/|$)", re.IGNORECASE),
]

DENY_COMMAND_PATTERNS = [
    (
        re.compile(r"\brm\s+[^\n]*(-[a-zA-Z]*r[a-zA-Z]*f|-[a-zA-Z]*f[a-zA-Z]*r|--recursive[^\n]*--force|--force[^\n]*--recursive)\b"),
        "禁止執行遞迴強制刪除指令 (rm -rf)。",
    ),
    (
        re.compile(r"(^|[;&|\s])sudo\b"),
        "禁止透過 sudo 提權執行指令。",
    ),
    (
        re.compile(r"\bgit\s+push\b[^\n]*(\s--force\b|\s-f\b|\s--force-with-lease\b)"),
        "禁止執行強制推送 (git push --force)。",
    ),
    (
        re.compile(r"\bgit\s+reset\s+--hard\b"),
        "禁止執行破壞性重設 (git reset --hard)。",
    ),
    (
        re.compile(r"\bgit\s+clean\b[^\n]*-[a-zA-Z]*f"),
        "禁止執行強制清除未追蹤檔案 (git clean -f)。",
    ),
    (
        re.compile(r"\b(keytool|jarsigner|apksigner)\b"),
        "禁止直接執行簽章金鑰相關工具。",
    ),
    (
        re.compile(
            r"(local\.properties|(?:keystore|signing)\.properties|google-services\.json|\.(?:jks|keystore|p12|pem|key)\b|(^|[\s/\"'=])\.env(\.[a-zA-Z0-9_-]+)?\b|app/release\b)",
            re.IGNORECASE,
        ),
        "禁止透過終端指令存取敏感檔案或 Release 產物。",
    ),
]

FORCE_ASK_COMMAND_PATTERNS = [
    (
        re.compile(r"(\./)?gradlew\b[^\n]*\b(assembleRelease|bundleRelease|publish\w*)\b"),
        "Release 打包或發布屬於高風險操作，請人工確認。",
    ),
    (
        re.compile(r"\bgit\s+push\b"),
        "推送至遠端倉庫 (git push) 需人工確認。",
    ),
    (
        re.compile(r"\badb\s+(uninstall|root|remount|pull|backup|restore|shell\s+pm\s+clear)\b"),
        "此 adb 指令會修改裝置狀態或擷取資料，需人工確認。",
    ),
]


def respond(decision: str, reason: str = "") -> None:
    payload = {"decision": decision}
    if reason:
        payload["reason"] = reason
    print(json.dumps(payload, ensure_ascii=False))
    sys.exit(0)


def normalize_path(raw_path: str, cwd: str) -> str:
    expanded = os.path.expanduser(raw_path)
    if not os.path.isabs(expanded) and cwd:
        expanded = os.path.join(cwd, expanded)
    return os.path.realpath(expanded)


def is_within_dir(target: str, base: str) -> bool:
    try:
        base_real = os.path.realpath(base)
        return os.path.commonpath([target, base_real]) == base_real
    except ValueError:
        return False


def matches_sensitive_file(raw_path: str, resolved_path: str, workspace_paths: list[str]) -> bool:
    candidates = [raw_path.replace("\\", "/"), resolved_path.replace("\\", "/")]
    for ws in workspace_paths:
        ws_real = os.path.realpath(ws)
        if is_within_dir(resolved_path, ws_real):
            rel = os.path.relpath(resolved_path, ws_real).replace("\\", "/")
            candidates.append(rel)

    for candidate in candidates:
        for pattern in SENSITIVE_FILE_PATTERNS:
            if pattern.search(candidate):
                return True
    return False


def check_file_tool(args: dict, cwd: str, workspace_paths: list[str], artifact_dir: str) -> None:
    allowed_roots = list(workspace_paths)
    if artifact_dir:
        allowed_roots.append(artifact_dir)
    allowed_roots.append("/tmp")

    for key in FILE_PATH_ARG_KEYS:
        val = args.get(key)
        if not isinstance(val, str) or not val.strip():
            continue

        resolved = normalize_path(val, cwd)

        if matches_sensitive_file(val, resolved, workspace_paths):
            respond("deny", f"專案安全規則禁止存取敏感路徑：{val}")

        if workspace_paths and not any(is_within_dir(resolved, root) for root in allowed_roots):
            respond("ask", f"目標路徑位於工作區之外，需人工確認：{val}")

    respond("allow")


def check_run_command(args: dict) -> None:
    cmd = args.get("CommandLine", "")
    if not isinstance(cmd, str):
        respond("force_ask", "無法解析終端指令內容，請人工確認。")

    for pattern, reason in DENY_COMMAND_PATTERNS:
        if pattern.search(cmd):
            respond("deny", f"{reason} (指令: {cmd})")

    for pattern, reason in FORCE_ASK_COMMAND_PATTERNS:
        if pattern.search(cmd):
            respond("force_ask", f"{reason} (指令: {cmd})")

    if args.get("BypassSandbox") is True:
        respond("ask", f"指令請求停用沙盒 (BypassSandbox)，需人工確認：{cmd}")

    respond("allow")


def main() -> None:
    try:
        data = json.load(sys.stdin)
    except Exception as exc:
        respond("force_ask", f"安全檢查腳本解析輸入失敗，轉為人工確認：{exc}")

    tool_call = data.get("toolCall") or {}
    tool_name = str(tool_call.get("name") or "")
    args = tool_call.get("args") or {}
    cwd = str(data.get("cwd") or "")
    workspace_paths = [str(p) for p in (data.get("workspacePaths") or []) if p]
    artifact_dir = str(data.get("artifactDirectoryPath") or "")

    if tool_name == "run_command":
        check_run_command(args)
    else:
        check_file_tool(args, cwd, workspace_paths, artifact_dir)


if __name__ == "__main__":
    main()
