@echo off
@echo MS Windows 2000 batch utilities
@echo Author: Behrad Zari(behrad@dev.java.net)
for /R %%d in (.) do (
	if exist %%d\CVS rmdir /S /Q %%d\CVS
)