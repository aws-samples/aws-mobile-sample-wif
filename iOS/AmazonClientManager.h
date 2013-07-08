/*
 * Copyright 2010-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

#import <AWSSecurityTokenService/AWSSecurityTokenService.h>
#import <FacebookSDK/FacebookSDK.h>
//Import AWS services here
#import <AWSS3/AWSS3.h>


#define FB_ROLE_ARN             @"Your Facebook Role ARN"

@interface AmazonClientManager:NSObject {}

@property (retain, nonatomic) FBSession *session;
-(void)reloadFBSession;
-(void)FBLogin;
-(void)FBLogout;

@property (retain, nonatomic) UIViewController *viewController;

+(AmazonClientManager *)sharedInstance;

//Add AWS Clients here
+(AmazonS3Client *)s3;



-(BOOL)isLoggedIn;
-(void)wipeAllCredentials;

@end
