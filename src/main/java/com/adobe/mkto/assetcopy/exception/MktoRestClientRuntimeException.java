/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.exception;

public class MktoRestClientRuntimeException extends RuntimeException {
    public MktoRestClientRuntimeException() {
        super();
    }

    public MktoRestClientRuntimeException(String message) {
        super(message);
    }

    public MktoRestClientRuntimeException(Throwable cause) {
        super(cause);
    }

    public MktoRestClientRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
