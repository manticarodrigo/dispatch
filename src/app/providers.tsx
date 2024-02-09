"use client";

import { useEffect } from "react";
import { Providers as CLJSProviders, init } from "~/ui.core";

export function Providers({ children }: { children: React.ReactNode }) {
  useEffect(() => {
    init();
  }, []);
  return <CLJSProviders>{children}</CLJSProviders>;
}
