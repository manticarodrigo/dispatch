"use client";

import { useQuery } from "@apollo/client";

import { UserScope } from "#/queries/user/fetch-scope.graphql";
import { useRouter } from "next/navigation";
import { DispatchIcon } from "~/ui.components.icons.dispatch";
import { useEffect } from "react";

export default function RootPage() {
  const router = useRouter();
  const { data, loading } = useQuery(UserScope);

  useEffect(() => {
    if (!loading) {
      const scope = data?.user.scope;

      if (scope === "organization") {
        router.push("/organization");
      } else if (scope === "agent") {
        router.push("/agent");
      } else {
        router.push("/landing");
      }
    }
  }, [router, data, loading]);

  return (
    <div className="flex justify-center items-center h-full w-full">
      <div className="animate-pulse">
        <DispatchIcon />
      </div>
    </div>
  );
}
