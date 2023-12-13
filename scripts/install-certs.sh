#!/bin/bash

# Installs certificates from Maven
#
# Make sure to:
# - Target the correct JDK and path
# - Use the correct certificates - you'll need to download the root and intermediary certificates as well

# Targets OpenJDK21
install-certs-jdk21 () {
	keytool -import -alias root-cer -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/openjdk-21.0.1/Contents/Home/lib/security/cacerts -file ~/Downloads/Cloud\ Services\ Root\ CA.cer
	keytool -import -alias inter-cer -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/openjdk-21.0.1/Contents/Home/lib/security/cacerts/ -file ~/Downloads/Cloud\ Services\ CA\ -\ G2.cer
	keytool -import -alias inter-cer2 -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/openjdk-21.0.1/Contents/Home/lib/security/cacerts -file ~/Downloads/SSL-SG1-GLOBAL.cer
	keytool -import -alias maven-cer -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/openjdk-21.0.1/Contents/Home/lib/security/cacerts -file ~/Downloads/repo.maven.apache.org.cer
}

# Targets JDK8 installed from HomeBrew
install-certs-jdk1.8 () {
	sudo keytool -import -alias root-cer -keystore /Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home/jre/lib/security/cacerts -file ~/Downloads/Cloud\ Services\ Root\ CA.cer
	sudo keytool -import -alias inter-cer -keystore /Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home/jre/lib/security/cacerts -file ~/Downloads/Cloud\ Services\ CA\ -\ G2.cer
	sudo keytool -import -alias inter-cer2 -keystore /Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home/jre/lib/security/cacerts -file ~/Downloads/SSL-SG1-GLOBAL.cer
	sudo keytool -import -alias maven-cer -keystore /Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home/jre/lib/security/cacerts -file ~/Downloads/repo.maven.apache.org.cer
}

# Targets JDK8 installed from IntelliJ
install-certs-temurin-jdk1.8 () {
	sudo keytool -import -alias root-cer -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/eclipse-temurin-1.8.0_392/Contents/Home/jre/lib/security/cacerts -file ~/Downloads/Cloud\ Services\ Root\ CA.cer
	sudo keytool -import -alias inter-cer -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/eclipse-temurin-1.8.0_392/Contents/Home/jre/lib/security/cacerts -file ~/Downloads/Cloud\ Services\ CA\ -\ G2.cer
	sudo keytool -import -alias inter-cer2 -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/eclipse-temurin-1.8.0_392/Contents/Home/jre/lib/security/cacerts -file ~/Downloads/SSL-SG1-GLOBAL.cer
	sudo keytool -import -alias maven-cer -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/eclipse-temurin-1.8.0_392/Contents/Home/jre/lib/security/cacerts -file ~/Downloads/repo.maven.apache.org.cer
}

# Deletes certificates from a specified keystore
delete-certs () {
  sudo keytool -delete -alias root-cer -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/temurin-1.8.0_392/Contents/Home/jre/lib/security/cacerts
  sudo keytool -delete -alias inter-cer -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/temurin-1.8.0_392/Contents/Home/jre/lib/security/cacerts
  sudo keytool -delete -alias inter-cer2 -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/temurin-1.8.0_392/Contents/Home/jre/lib/security/cacerts
  sudo keytool -delete -alias maven-cer -keystore /Users/mayur.shankar/Library/Java/JavaVirtualMachines/temurin-1.8.0_392/Contents/Home/jre/lib/security/cacerts
}
