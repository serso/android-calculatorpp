from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
import time

import sys

outFolder = sys.argv[1]
outFilename = sys.argv[2]

print ''
print 'Screenshot will be located in ' + outFolder + ' with name ' + outFilename;

apk = '/home/serso/projects/java/android/calculatorpp/calculatorpp/target/calculatorpp-1.5.2-SNAPSHOT.apk'
package = 'org.solovyev.android.calculator'
activity = 'org.solovyev.android.calculator.CalculatorActivity'
deviceName = 'emulator-5554'

def takeScreenshot (folder, filename):
    screenshot = device.takeSnapshot()
    screenshot.writeToFile(folder + '/' + filename + '.png','png')
    return

print 'Waiting for device ' + deviceName + '...'
device = MonkeyRunner.waitForConnection(30, deviceName)

if device:
    # unlock device
    device.wake()
    device.drag((130, 620), (500, 620), 1.0, 120)

    print 'Device found, removing application if any ' + package + '...'
    device.removePackage(package)

    print 'Installing apk ' + apk + '...'
    device.installPackage(apk)

    runComponent = package + '/' + activity

    print 'Starting activity ' + runComponent + '...'
    device.startActivity(component=runComponent)

    # close all dialogs
    device.shell('input keyevent 4')

    # sleep while application will be loaded
    MonkeyRunner.sleep(2);

    print 'Taking screenshot...'
    takeScreenshot(outFolder, outFilename + '_' + str(time.time()) );

    print 'Changing orientation...'

    print '#########'
    print 'Finished!'
    print '#########'
else:
    print '#########'
    print 'Failure!'
    print '#########'
