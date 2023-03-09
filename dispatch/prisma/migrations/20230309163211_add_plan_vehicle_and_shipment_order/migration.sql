/*
  Warnings:

  - You are about to drop the `_PlanToShipment` table. If the table is not empty, all the data it contains will be lost.
  - You are about to drop the `_PlanToVehicle` table. If the table is not empty, all the data it contains will be lost.

*/
-- DropForeignKey
ALTER TABLE "_PlanToShipment" DROP CONSTRAINT "_PlanToShipment_A_fkey";

-- DropForeignKey
ALTER TABLE "_PlanToShipment" DROP CONSTRAINT "_PlanToShipment_B_fkey";

-- DropForeignKey
ALTER TABLE "_PlanToVehicle" DROP CONSTRAINT "_PlanToVehicle_A_fkey";

-- DropForeignKey
ALTER TABLE "_PlanToVehicle" DROP CONSTRAINT "_PlanToVehicle_B_fkey";

-- DropTable
DROP TABLE "_PlanToShipment";

-- DropTable
DROP TABLE "_PlanToVehicle";

-- CreateTable
CREATE TABLE "PlanVehicle" (
    "id" TEXT NOT NULL,
    "order" INTEGER NOT NULL,
    "planId" TEXT NOT NULL,
    "vehicleId" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "PlanVehicle_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "PlanShipment" (
    "id" TEXT NOT NULL,
    "order" INTEGER NOT NULL,
    "planId" TEXT NOT NULL,
    "shipmentId" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "PlanShipment_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "PlanVehicle_id_key" ON "PlanVehicle"("id");

-- CreateIndex
CREATE UNIQUE INDEX "PlanShipment_id_key" ON "PlanShipment"("id");

-- AddForeignKey
ALTER TABLE "PlanVehicle" ADD CONSTRAINT "PlanVehicle_planId_fkey" FOREIGN KEY ("planId") REFERENCES "Plan"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "PlanVehicle" ADD CONSTRAINT "PlanVehicle_vehicleId_fkey" FOREIGN KEY ("vehicleId") REFERENCES "Vehicle"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "PlanShipment" ADD CONSTRAINT "PlanShipment_planId_fkey" FOREIGN KEY ("planId") REFERENCES "Plan"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "PlanShipment" ADD CONSTRAINT "PlanShipment_shipmentId_fkey" FOREIGN KEY ("shipmentId") REFERENCES "Shipment"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
