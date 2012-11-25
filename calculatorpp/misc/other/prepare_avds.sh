#!/bin/bash


declare -a densities=("160" "213" "240" "320")
declare -a resolutions=("320x480" "480x640" "480x800" "480x854" "640x960" "1024x600" "1024x768" "1280x768")
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

            echo "Creating AVD $name"
            echo "Density: $density"
            echo "Resolution: $resolution"
            echo "Target: $target"

            $ANDROID_HOME/tools/android -s create avd -n $name -t $target -b x86 --force -s $resolution

            if grep -R "hw.lcd.density" $HOME/.android/avd/$name.avd/config.ini
            then
                # replace density in config.ini
                sed -i "s/hw.lcd.density=240/hw.lcd.density=$density/g" $HOME/.android/avd/$name.avd/config.ini
            else
                # code if not found
                echo "hw.lcd.density=$density" >> $HOME/.android/avd/$name.avd/config.ini
            fi

            echo "sdcard.size=64M" >> $HOME/.android/avd/$name.avd/config.ini
            echo "vm.heapSize=48" >> $HOME/.android/avd/$name.avd/config.ini
            echo "hw.ramSize=256" >> $HOME/.android/avd/$name.avd/config.ini

            #arr=(${resolution//x/ })

            #echo "hw.lcd.width = ${arr[0]}" >> $HOME/.android/avd/$name.avd/config.ini
            #echo "hw.lcd.height = ${arr[1]}" >> $HOME/.android/avd/$name.avd/config.ini

        done
    done
done

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
            $ANDROID_HOME/tools/monkeyrunner ./wait_device.py
            $ANDROID_HOME/platform-tools/adb -s emulator-5580 emu kill

        done
    done
done


