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

#import "AmazonClientManager.h"
#import <AWSRuntime/AWSRuntime.h>
#import "AmazonKeyChainWrapper.h"
#import <AWSSecurityTokenService/AWSSecurityTokenService.h>

static AmazonWIFCredentialsProvider *wif = nil;

//Add static instance of each AWS client 
static AmazonS3Client *s3 = nil;

@implementation AmazonClientManager

@synthesize viewController=_viewController;
@synthesize session = _session;


+ (AmazonClientManager *)sharedInstance
{
    static AmazonClientManager *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[AmazonClientManager alloc] init];
    });
    return sharedInstance;
}

//Add methods for returning shared instances of each AWS client
+(AmazonS3Client *)s3
{
    return s3;
}



-(BOOL)isLoggedIn
{
    return ( [AmazonKeyChainWrapper username] != nil && wif != nil);
}

-(void)initClients
{
    if (wif != nil) {
        //Change to appropriate clients
        [s3 release];
        s3  = [[AmazonS3Client alloc] initWithCredentialsProvider:wif];
    }
}

-(void)wipeAllCredentials
{
    @synchronized(self)
    {        
        //Change to appropriate clients
        [s3 release];
        s3 = nil;
        [[AmazonClientManager sharedInstance] FBLogout];
    }
}

+(UIAlertView *)errorAlert:(NSString *)message
{
    return [[[UIAlertView alloc] initWithTitle:@"Error" message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] autorelease];
}




#pragma mark - Facebook

-(void)reloadFBSession
{
    if (!self.session.isOpen) {
        // create a fresh session object
        self.session = [[[FBSession alloc] init] autorelease];
        
        // if we don't have a cached token, a call to open here would cause UX for login to
        // occur; we don't want that to happen unless the user clicks the login button, and so
        // we check here to make sure we have a token before calling open
        if (self.session.state == FBSessionStateCreatedTokenLoaded) {
            
            // even though we had a cached token, we need to login to make the session usable
            [self.session openWithCompletionHandler:^(FBSession *session,
                                                      FBSessionState status,
                                                      NSError *error) {
                if (error != nil) {
                    [[AmazonClientManager errorAlert:[NSString stringWithFormat:@"Error logging in with FB: %@", error.description]] show];
                }
            }];
        }
    }
}


-(void)CompleteFBLogin
{
    
    wif = [[AmazonWIFCredentialsProvider alloc] initWithRole:FB_ROLE_ARN
                                          andWebIdentityToken:self.session.accessTokenData.accessToken
                                                 fromProvider:@"graph.facebook.com"];
    
    // if we have an id, we are logged in
    if (wif.subjectFromWIF != nil) {
        NSLog(@"IDP id: %@", wif.subjectFromWIF);
        [AmazonKeyChainWrapper storeUsername:wif.subjectFromWIF];
        
        [self initClients];
        [self.viewController dismissModalViewControllerAnimated:NO];
    }
    else {
        [[AmazonClientManager errorAlert:@"Unable to assume role, please check logs for error"] show];
    }
}

-(void)FBLogin
{
    // session already open, exit
    if (self.session.isOpen) {
        [self CompleteFBLogin];
        return;
    }

    if (self.session.state != FBSessionStateCreated) {
        // Create a new, logged out session.
        self.session = [[[FBSession alloc] init] autorelease];
    }
    
    [self.session openWithCompletionHandler:^(FBSession *session,
                                              FBSessionState status,
                                              NSError *error) {
        if (error != nil) {
            [[AmazonClientManager errorAlert:[NSString stringWithFormat:@"Error logging in with FB: %@", error.description]] show];
        }
        else {
            [self CompleteFBLogin];
        }
    }];
    
}

-(void)FBLogout
{
    [[FBSession activeSession] close];
}


@end
