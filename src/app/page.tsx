"use client";

import { useEffect } from "react";
import { react_app as ReactApp, init } from "../../out/app/ui.core";

export default function Home() {
  useEffect(() => {
    init();
  }, []);

  return <ReactApp />;
}
