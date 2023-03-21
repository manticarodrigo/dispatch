/*
  Warnings:

  - You are about to drop the column `windows` on the `Shipment` table. All the data in the column will be lost.
  - A unique constraint covering the columns `[externalId,organizationId]` on the table `Agent` will be added. If there are existing duplicate values, this will fail.
  - A unique constraint covering the columns `[externalId,organizationId]` on the table `Place` will be added. If there are existing duplicate values, this will fail.
  - A unique constraint covering the columns `[externalId,organizationId]` on the table `Shipment` will be added. If there are existing duplicate values, this will fail.
  - A unique constraint covering the columns `[externalId,organizationId]` on the table `Vehicle` will be added. If there are existing duplicate values, this will fail.

*/
-- AlterTable
ALTER TABLE "Agent" ADD COLUMN     "externalId" TEXT;

-- AlterTable
ALTER TABLE "Place" ADD COLUMN     "externalId" TEXT;

-- AlterTable
ALTER TABLE "Shipment" DROP COLUMN "windows",
ADD COLUMN     "archived" BOOLEAN NOT NULL DEFAULT false,
ADD COLUMN     "externalId" TEXT;

-- AlterTable
ALTER TABLE "Vehicle" ADD COLUMN     "externalId" TEXT;

-- CreateTable
CREATE TABLE "ShipmentWindow" (
    "id" TEXT NOT NULL,
    "shipmentId" TEXT NOT NULL,
    "startAt" TIMESTAMP(3) NOT NULL,
    "endAt" TIMESTAMP(3) NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "ShipmentWindow_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "ShipmentWindow_id_key" ON "ShipmentWindow"("id");

-- CreateIndex
CREATE UNIQUE INDEX "Agent_externalId_organizationId_key" ON "Agent"("externalId", "organizationId");

-- CreateIndex
CREATE UNIQUE INDEX "Place_externalId_organizationId_key" ON "Place"("externalId", "organizationId");

-- CreateIndex
CREATE UNIQUE INDEX "Shipment_externalId_organizationId_key" ON "Shipment"("externalId", "organizationId");

-- CreateIndex
CREATE UNIQUE INDEX "Vehicle_externalId_organizationId_key" ON "Vehicle"("externalId", "organizationId");

-- AddForeignKey
ALTER TABLE "ShipmentWindow" ADD CONSTRAINT "ShipmentWindow_shipmentId_fkey" FOREIGN KEY ("shipmentId") REFERENCES "Shipment"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
