/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.exception;

public class AssetCopyRuntimeException extends RuntimeException {

    public AssetCopyRuntimeException() {
        super();
    }

    public AssetCopyRuntimeException(String message) {
        super(message);
    }

    public AssetCopyRuntimeException(Throwable cause) {
        super(cause);
    }

    public AssetCopyRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
