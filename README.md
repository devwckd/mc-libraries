# mc-libraries
![GitHub Repo stars](https://img.shields.io/github/stars/devwckd/mc-libraries?color=orange&style=for-the-badge)
![GitHub issues](https://img.shields.io/github/issues/devwckd/mc-libraries?color=orange&style=for-the-badge)
![GitHub last commit](https://img.shields.io/github/last-commit/devwckd/mc-libraries?color=orange&style=for-the-badge)

MC-libraries is a heavily reflection based library inspired by Spring, NestJS and others.  
Need help? contact me on [twitter](https://twitter.com/devwckd) or message me on discord **devwckd#8790**.

### Content
* [Dependency](#dependency)
* [Modules](#modules)
* Documentation (WIP)
* [Pull Requests](#pull-requests)
* [Ending](#ending)

## Dependency
In order to user mc-libraries you'll need the following on your build.gradle
```gradle
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.devwckd:mc-libraries:VERSION'
}
```

## Modules
| Name   | Description                                                          |
| ------ | -------------------------------------------------------------------- |
| core   | Holds all the library's functionalities                              | 
| spigot | Implements core and adds extra utils stuff to the spigot environment |
| bungee | Implements core to the bungee environment                            |

## Pull Requests
PRs are welcome as long as they are well explained and only change one feature at a time.  
This doesn't mean i'm gonna accept all PRs, it just means i'm gonna consider it.

## Ending
If you liked and/or use the libraries consider leaving a star on the repo and following me on twitter :)
