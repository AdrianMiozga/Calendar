#Requires AutoHotkey v2.0
#SingleInstance Force

; Win + Alt + ArrowUp
#!Up:: CenterActiveWindow()

CenterActiveWindow()
{
    currentWindowHandle := WinExist("A")

    if (!currentWindowHandle) {
        return
    }

    monitorInfo := Buffer(40)

    ; Set cbSize
    ; https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-getmonitorinfoa#parameters
    NumPut("UInt", monitorInfo.Size, monitorInfo, 0)

    static MONITOR_DEFAULTTONEAREST := 0x00000002

    hMonitor := DllCall("User32.dll\MonitorFromWindow",
        "UInt", currentWindowHandle,
        "UInt", MONITOR_DEFAULTTONEAREST)

    DllCall("User32.dll\GetMonitorInfo",
        "Ptr", hMonitor,
        "Ptr", monitorInfo.Ptr,
        "Int")

    workAreaLeft := NumGet(monitorInfo, 20, "Int")
    workAreaTop := NumGet(monitorInfo, 24, "Int")
    workAreaRight := NumGet(monitorInfo, 28, "Int")
    workAreaBottom := NumGet(monitorInfo, 32, "Int")

    scale := 0.6

    windowWidth := (workAreaRight - workAreaLeft) * scale
    windowHeight := (workAreaBottom - workAreaTop) * scale

    windowX := workAreaLeft +
        ((workAreaRight - workAreaLeft) - windowWidth) / 2
    windowY := workAreaTop +
        ((workAreaBottom - workAreaTop) - windowHeight) / 2

    WinMove(windowX, windowY, windowWidth, windowHeight, currentWindowHandle)
}
