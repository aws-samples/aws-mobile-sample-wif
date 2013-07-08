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
package com.amazonaws.demo.s3uploader;

public interface WIFIdentityProvider {

    // returns the OAuth/OpenID token for the provider
    public String getToken();

    // returns the provider ID
    public String getProviderID();

    // return the role ARN to use
    public String getRoleARN();

    // calls necessary functions to logout with this IdP
    public void logout();
}
