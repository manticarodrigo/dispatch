"use client";

import React, { useLayoutEffect } from "react";
import { redirect } from "next/navigation";

import { BaseLoader } from "~/ui.components.loaders.base";
import { useApolloClient } from "@apollo/client";

export default function LogoutPage() {
  const client = useApolloClient();

  useLayoutEffect(() => {
    client.resetStore();

    document.cookie =
      "sessionId=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";

    return redirect("/login");
  });

  return <BaseLoader />;
}
