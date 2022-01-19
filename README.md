# CardReader Demo

# Huawei Mobile Services
Copyright (c) Huawei Technologies Co., Ltd. 2012-2021. All rights reserved.

## Table of Contents
* [Introduction](#introduction)
* [What you will Create](#what-you-will-create)
* [What You Will Learn](#what-you-will-learn)
* [Hardware Requirements](#hardware-requirements)
* [Software Requirements](#software-requirements)
* [Example Code](#example-code)
* [License](#license)

## Introduction :

CardReader App will give insight about how HMS ML kit, Scan kit, and Fido kit can be used for Business&Office purpose category and how we can make the card scanning 
easy. This app demonstrate on how end user can scan the Bank Cards, General Cards(Business, PAN Card and Adhaar Card) and save it in the app. Whenver required User can open the app and see the stored card details. As a Safety purpose the Details of the card can only be seen after authentication using PIN Lock or FingerPrint or 
3D-FaceRecognition. App is developed using below HMS Features.

## What You Will Create

In this code lab, you will create a CardReader Demo project and use the APIs of HUAWEI ML, Scan and Fido. We are going to create a end to end prototype for scanning the different type of cards (General,Bank cards) and store it in the app.

*	Recognizing bank cards and general cards using ML Kit
*	Generate QR code and share
*	PIN/Fingerprint/Face Authentication using FIDO kit

## What You Will Learn

In this code lab, you will learn how to:
*	Integrate HUAWEI ML Kit.
*	Integrate Scan Kit. 
*	Integrate Fido .

## What You Will Need

### Hardware Requirements

*	A computer (desktop or laptop) that runs the Windows 10 operating system
*	Huawei phone with HMS Core (APK) 5.0.0.300 or later installed
```
**Note:** Please prepare the preceding hardware environment and relevant devices in advance.
```
### Software Requirements

*	[Android Studio 3.X](https://developer.android.com/studio)
*	JDK 1.8 and later 
*	SDK Platform 23 and later
*	Gradle 4.6 and later

```
**Note:** Please prepare the preceding software environment in advance.
> Only EMUI 4 (API Level 23) and later versions support fingerprint authentication. In addition, ensure that the mobile phone hardware supports fingerprint authentication
> Only EMUI 10 (API Level 29) and later versions support 3D facial authentication. In addition, the device hardware must support 3D facial authentication. Currently, only Mate 20 Pro and Mate 30 Pro support 3D facial authentication.
```

## Prepare Initial configuration

Use the below link to do initial configuration for the application development
https://developer.huawei.com/consumer/en/codelab/HMSPreparation/index.html#3

## Enable HUAWEI Service(s) in AGC console

Enable the API permission for below kits from Project Settings, Manage APIs and enable the API permission.
*	ML Kit
*	Fido Kit
*	Scan Kit (is a standalone sdk, no need of any configuration in AGC)

```
**Note:** Some API’s will be enabled by default. If not enable it manually.
```

## Integrating HMS SDK
For official Documentation and more services, please refer below documentation from developer website

1.	[ML Kit(Bank Card Recognition Service)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/bank-card-recognition-0000001050038118)

2.	[ML Kit(General Card Recognition Service)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/bank-card-recognition-0000001050038118)

3.	[Scan Kit](https://developer.huawei.com/consumer/en/codelab/ScanKit/index.html#3)

4.	[Fido](https://developer.huawei.com/consumer/en/codelab/HMSCoreFIDOBioAuthn/index.html#4)


## Example Code

AgreementPageView: Displays agreement page to user to utilize device authentication. User should agree to proceed further to use the application.

DashboardActivity: Application Main Dash Board, which contains GeneralCard, BankCard and Settings grid view in the main page.

GeneralCardActivity: Dispays the Add Card page to scan Businesscard, pancard and adhaar card

ScannedCardListActivity: Contains the list of saved General Cards (Businesscard, pancard and adhaar card)

GeneralcardListAdapter: Adapter to handle the General card list

AadhaarProcessing: Contains processing logic for scanned text from Aadhaar card.

GenericProcessing: Contains processing logic for scanned text from Business Card.

PanProcessing: Contains processing logic for scanned text from Pan Card

BankCard: Dispays the Add Card page to scan BankCard. Only VISA/Master Card are supported as of now.

BankCardListAdapter: Contains the list of saved bank cards.

ScannedCardDetailsActivity: Displays the Full details of the card

AuthenticationPage: prompt the user to show authenication page to enter PIN lock or Fingerprint or FaceRecognition, to access the card details. If user wants to View or delete the card, this page appears to make sure user is the actual owner of the device.

BioAuthUtils: Contains common functions used inside the app to check Device authentication.

AppDatabase,BusinessCardEntity, BusinessCardEntityDao, DatabaseClient: Used for roomdb functionality

SettingsActivity: Settings menu, contains version Info and Change Security Settings menu

Constants: Contains the constants used inside the application

## References

*	[ML Kit(General Card Recognition)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/general-card-recognition-0000001050040073)
*	[ML Kit (Bank Card Recognition)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/bank-card-recognition-0000001050038118)
*	[Scan Kit(Generating QR Code)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/scan-generate-barcode-0000001050995005)
*	[Fingerprint Authentication using FIDO kit](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/bioauthn-sdk-0000001055606575)
*	[Face Recognition using FIDO Kit](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/bioauthn-sdk-0000001055606575)

## License
HMS Guide sample is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).# CardReader
