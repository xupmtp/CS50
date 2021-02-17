package edu.harvard.cs50.wordcard.ui.login;

import androidx.annotation.Nullable;

import edu.harvard.cs50.wordcard.model.Users;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private Users success;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginResult(@Nullable Users success) {
        this.success = success;
    }

    @Nullable
    Users getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}