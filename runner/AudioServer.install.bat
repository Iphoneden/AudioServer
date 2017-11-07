SET CDIR=%~dp0
SET BIN_PATH=%CDIR%AudioServer.run.bat

call sc create AudioServer binPath= %BIN_PATH%
call sc config AudioServer start= auto
call sc description AudioServer "Служба позволяет контролировать звуковые устройства"