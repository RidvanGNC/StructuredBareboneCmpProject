package org.structured_barebone.di

import org.koin.core.module.Module

/**
 * Platform-specific definitions: the preferences store (`Settings`) and the `SecureKeyStore`. Preferences are
 * plain text on every platform; tokens, keys and other secrets belong in the secure store only.
 */
expect val platformModule: Module
