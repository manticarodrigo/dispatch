import { CapacitorConfig } from "@capacitor/cli";

const appIdMap = {
  prod: "app.ambito.dispatch",
  dev: "dev.ambito.dispatch",
};
const hostnameMap = {
  prod: "dispatch.ambito.app",
  dev: "dispatch.ambito.dev",
};

const config: CapacitorConfig = {
  appId: appIdMap[process.env.STAGE],
  appName: "Dispatch",
  webDir: "public",
  bundledWebRuntime: false,
  server: {
    url: process.env.RELOAD_URL,
    cleartext: process.env.RELOAD_URL && true,
    hostname: hostnameMap[process.env.STAGE],
    androidScheme: "https",
  },
  plugins: {
    CapacitorUpdater: {
      updateUrl: process.env.API_URL && `${process.env.API_URL}/update`,
    },
  },
};

export default config;
