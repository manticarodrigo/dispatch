import { CapacitorConfig } from "@capacitor/cli";

const config: CapacitorConfig = {
  appId:
    process.env.STAGE === "prod"
      ? "app.ambito.dispatch"
      : "dev.ambito.dispatch",
  appName: "dispatch",
  webDir: "public",
  bundledWebRuntime: false,
  server: {
    url: process.env.RELOAD_URL,
  },
  plugins: {
    CapacitorUpdater: {
      updateUrl: process.env.API_URL && `${process.env.API_URL}/update`,
      autoUpdate: Boolean(process.env.API_URL),
    },
  },
};

export default config;
