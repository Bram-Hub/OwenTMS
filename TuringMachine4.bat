@echo off

echo %0
echo %1
if [%1]==[] goto noargs
set bat_dir=%~dp0%
java -jar "%bat_dir%TuringMachine4.jar" %1
goto :end

:noargs
java -jar TuringMachine4.jar

:end
PAUSE