import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { defaultLanguage, dictionaries, languageCodes, type LanguageCode, type MessageKey } from "./dictionaries";

/** The user's choice: a specific language, or "system" to follow the browser language. */
export type LanguageChoice = LanguageCode | "system";

const storageKey = "language";

export type Translate = (key: MessageKey, values?: Readonly<Record<string, string | number>>) => string;

interface I18nValue {
  readonly choice: LanguageChoice;
  readonly language: LanguageCode;
  readonly setChoice: (choice: LanguageChoice) => void;
  readonly t: Translate;
}

const I18nContext = createContext<I18nValue | undefined>(undefined);

function isLanguageCode(value: string): value is LanguageCode {
  return (languageCodes as string[]).includes(value);
}

function readStoredChoice(): LanguageChoice {
  // Storage can throw (private windows, blocked site data); the app must work without it.
  try {
    const stored = window.localStorage.getItem(storageKey);
    if (stored !== null && (stored === "system" || isLanguageCode(stored))) {
      return stored;
    }
  } catch {
    // Ignored on purpose: fall back to following the browser language.
  }
  return "system";
}

function writeStoredChoice(choice: LanguageChoice): void {
  try {
    window.localStorage.setItem(storageKey, choice);
  } catch {
    // Ignored on purpose: the choice then lasts until the page is closed.
  }
}

function browserLanguage(): LanguageCode {
  for (const tag of navigator.languages) {
    const code = tag.toLowerCase().split("-")[0] ?? "";
    if (isLanguageCode(code)) {
      return code;
    }
  }
  return defaultLanguage;
}

export function I18nProvider({ children }: { readonly children: ReactNode }) {
  const [choice, setChoiceState] = useState<LanguageChoice>(readStoredChoice);
  const language = choice === "system" ? browserLanguage() : choice;

  useEffect(() => {
    document.documentElement.lang = language;
  }, [language]);

  const setChoice = useCallback((next: LanguageChoice) => {
    writeStoredChoice(next);
    setChoiceState(next);
  }, []);

  const value = useMemo<I18nValue>(() => {
    const dictionary = dictionaries[language];
    const t: Translate = (key, values) =>
      dictionary[key].replace(/\{(\w+)\}/g, (placeholder, name: string) => String(values?.[name] ?? placeholder));
    return { choice, language, setChoice, t };
  }, [choice, language, setChoice]);

  return <I18nContext.Provider value={value}>{children}</I18nContext.Provider>;
}

export function useI18n(): I18nValue {
  const value = useContext(I18nContext);
  if (value === undefined) {
    throw new Error("useI18n must be used inside I18nProvider.");
  }
  return value;
}
