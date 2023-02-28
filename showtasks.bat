call runcrud.bat
if "%ERRORLEVEL%" == "0" goto openie
echo RUNCRUD errors
goto fail

:openie
@rem wait for tomcat to actually start
timeout /t 12 /nobreak

start iexplore "http://localhost:8080/crud/v1/tasks"
goto end

:fail
echo.
echo There were errors

:end
echo Work is finished