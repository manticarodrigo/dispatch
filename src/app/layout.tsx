import "./globals.css";
import type { Metadata } from "next";
import { Inter } from "next/font/google";
import { Analytics } from "@vercel/analytics/react";
import { Providers } from "./providers";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Dispatch",
  description: "Route planning and realtime fleet management",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className="w-full h-full">
      <body
        className={
          inter.className + " w-full h-full text-neutral-50 bg-neutral-900"
        }
        suppressHydrationWarning
      >
        <Providers>{children}</Providers>
        <div id="map-bench">
          <div id="map-container" className="w-full h-full"></div>
        </div>
        <Analytics />
      </body>
    </html>
  );
}
