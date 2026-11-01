# R8 rules for the release build. Libraries ship their own consumer rules (Ktor, kotlinx.serialization,
# Koin, Compose); add a rule here only for a failure that a release run actually showed.

# Keep line numbers in crash reports; the mapping file de-obfuscates them.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
