import { CapacitorConfig } from "@capacitor/cli";

const config: CapacitorConfig = {
  appId:
    process.env.STAGE === "prod"
      ? "app.ambito.dispatch"
      : "dev.ambito.dispatch",
  appName: "dispatch",
  webDir: "public",
  bundledWebRuntime: false,
  plugins: {
    CapacitorUpdater: {
      updateUrl: `${process.env.API_URL}/update`,
    },
  },
};

export default config;
