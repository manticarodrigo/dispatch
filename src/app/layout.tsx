import "./globals.css";
import type { Metadata } from "next";
import { Inter } from "next/font/google";

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
        {children}
        <div id="map-bench">
          <div id="map-container" className="w-full h-full"></div>
        </div>
      </body>
    </html>
  );
}
