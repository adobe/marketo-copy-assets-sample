/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.exception;

public class AssetCopyException extends Exception {

    public AssetCopyException() {
        super();
    }

    public AssetCopyException(String message) {
        super(message);
    }

    public AssetCopyException(String message, Throwable cause) {
        super(message, cause);
    }
}
