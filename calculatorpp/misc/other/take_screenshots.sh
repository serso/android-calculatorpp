#!/bin/bash

predefined=0

# first predefined

if [ $predefined -eq 1 ]
then
    declare -a names=("AVD_Galaxy_Tab" "AVD_Nexus_S_by_Google" "AVD_Nexus_One_by_Google" "AVD_Nexus_7_by_Google" "AVD_Galaxy_Nexus_by_Google")
    for name in ${names[@]}
    do
                $ANDROID_HOME/tools/emulator -ports 5580,5581 -avd $name &
                sleep 50
                $ANDROID_HOME/tools/monkeyrunner ./take_screenshots.py ~/projects/java/android/calculatorpp/calculatorpp/misc/other/tmp/2012.11.25 $name
                $ANDROID_HOME/platform-tools/adb -s emulator-5580 emu kill
                sleep 3
    done
fi

# then all others

#declare -a densities=("160" "213" "240" "320")
declare -a densities=("213" "240" "320")
#declare -a resolutions=("480x640" "480x800" "480x854" "640x960" "1024x600" "1024x768" "1280x768")
declare -a resolutions=("480x640")
declare -a targets=("android-16")

for target in ${targets[@]}
do
    for density in ${densities[@]}
    do

        for resolution in ${resolutions[@]}
        do
            name="AVD"
            name="$name$density"
            name="$name$resolution"
            name="$name$target"

            $ANDROID_HOME/tools/emulator -ports 5580,5581 -avd $name &
            sleep 5
            $ANDROID_HOME/tools/monkeyrunner ./take_screenshots.py ~/projects/java/android/calculatorpp/calculatorpp/misc/other/tmp/2012.11.25 $name
            $ANDROID_HOME/platform-tools/adb -s emulator-5580 emu kill
            sleep 3

        done
    done
done