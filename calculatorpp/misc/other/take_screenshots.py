from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
import time

import sys

outFolder = sys.argv[1]
outFilename = sys.argv[2]

print ''
print 'Screenshot will be located in ' + outFolder + ' with name ' + outFilename;

apk = '/home/serso/projects/java/android/calculatorpp/calculatorpp/target/calculatorpp.apk'
package = 'org.solovyev.android.calculator'
activity = 'org.solovyev.android.calculator.CalculatorActivity'
deviceName = 'emulator-5580'

def takeScreenshot (folder, filename):
    screenshot = device.takeSnapshot()
    screenshot.writeToFile(folder + '/' + filename + '.png','png')
    return

print 'Waiting for device ' + deviceName + '...'
device = MonkeyRunner.waitForConnection(100, deviceName)

if device:

    print 'Device found, removing application if any ' + package + '...'
    device.removePackage(package)

    print 'Installing apk ' + apk + '...'
    device.installPackage(apk)

    runComponent = package + '/' + activity

    print 'Starting activity ' + runComponent + '...'
    device.startActivity(component=runComponent)

    # sleep while application will be loaded
    MonkeyRunner.sleep(3);

    print 'Taking screenshot...'
    #outFilename = outFilename + '_' + str(time.time())
    takeScreenshot(outFolder, outFilename);

    print 'Changing orientation...'

    print '#########'
    print 'Finished!'
    print '#########'
else:
    print '#########'
    print 'Failure!'
    print '#########'
