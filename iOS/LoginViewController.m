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

#import "LoginViewController.h"
#import "AmazonClientManager.h"

//Import other view controllers here
#import "YourProjectsViewController.h"

@implementation LoginViewController

- (void)viewDidLoad
{
    [[AmazonClientManager sharedInstance] reloadFBSession];
    [super viewDidLoad];

}

- (void)viewWillAppear:(BOOL)animated
{
    [AmazonClientManager sharedInstance].viewController = self;
    [super viewWillAppear:animated];
    
}

- (void)viewWillDisappear:(BOOL)animated
{
    [AmazonClientManager sharedInstance].viewController = nil;
    [super viewWillDisappear:animated];
}

-(IBAction)FBlogin:(id)sender
{
    [[AmazonClientManager sharedInstance] FBLogin];
    if ([[AmazonClientManager sharedInstance] isLoggedIn])
    {
        /*
        *   YourProjectsViewController *projectViewController =[YourProjectsViewController new];
        *   [self presentModalViewController:projectViewController animated:NO];
        */
    }
}

-(void)dealloc
{
    [super dealloc];
}

@end
