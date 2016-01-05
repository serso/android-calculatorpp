#!/bin/bash

predefined=1
scale=0.51

# first predefined

if [ $predefined -eq 1 ]
then
    declare -a names=("AVD_Nexus_7_by_Google")
    for name in ${names[@]}
    do
                $ANDROID_HOME/tools/emulator -ports 5580,5581 -avd $name -scale $scale &
                sleep 50
                $ANDROID_HOME/tools/monkeyrunner ./take_screenshots.py ~/projects/java/android/calculatorpp/calculatorpp/misc/other/tmp/2012.11.25 $name
                $ANDROID_HOME/platform-tools/adb -s emulator-5580 emu kill
                sleep 3
    done
fi