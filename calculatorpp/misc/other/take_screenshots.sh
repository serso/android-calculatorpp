#!/bin/bash

declare -a densities=("160" "213" "240" "320")
declare -a resolutions=("320x480" "480x640" "480x800" "480x854" "640x960" "1024x600" "1024x768" "1280x768" "1536x1152" "1920x1200")
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

            $ANDROID_HOME/tools/emulator -avd $name &
            $ANDROID_HOME/tools/monkeyrunner ./take_screenshots.py ~/projects/java/android/calculatorpp/calculatorpp/misc/other/tmp $name
            $ANDROID_HOME/platform-tools/adb -s emulator-5554 emu kill

        done
    done
done