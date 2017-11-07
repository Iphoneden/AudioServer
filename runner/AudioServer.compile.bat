SET CDIR=%CD%
cd ..

call mvn clean install

cd %CDIR%
