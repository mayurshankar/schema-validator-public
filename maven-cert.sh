#!/bin/bash


# Installs certificates from Maven
#
# Make sure to:
# - Target the correct JDK
# - Use the correct certificate

install-certs () {
	keytool -import -alias root-cer -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/openjdk-21.0.1/Contents/Home/lib/security -file ~/Downloads/Cloud\ Services\ Root\ C.cer
	keytool -import -alias inter-cer -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/openjdk-21.0.1/Contents/Home/lib/security -file ~/Downloads/Cloud\ Services\ CA\ -\ G2.cer
	keytool -import -alias inter-cer2 -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/openjdk-21.0.1/Contents/Home/lib/security -file ~/Downloads/SSL-SG1-GLOBAL.cer
	keytool -import -alias maven-cer -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/openjdk-21.0.1/Contents/Home/lib/security -file ~/Downloads/repo.maven.apache.org.cer
}

