"use client"

import { react_providers as ReactProviders } from "../../out/app/ui.core";

export function Providers({ children }: { children: React.ReactNode }) {
  return <ReactProviders>{children}</ReactProviders>;
}
