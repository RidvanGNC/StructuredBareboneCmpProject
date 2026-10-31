package org.structured_barebone.di

import org.koin.core.module.Module

/**
 * Platform-specific definitions: the preferences store (`Settings`). Preferences are plain text on every
 * platform; never keep tokens, keys or other secrets there.
 */
expect val platformModule: Module
