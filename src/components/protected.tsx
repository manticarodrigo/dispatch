"use client";

import React, { useLayoutEffect } from "react";
import { redirect } from "next/navigation";
import { parse } from "cookie";

export function Protected({ children }: { children: React.ReactNode }) {
  useLayoutEffect(() => {
    const { sessionId } = parse(document.cookie);
    if (!sessionId) {
      return redirect("/login");
    }
  });

  return children;
}
