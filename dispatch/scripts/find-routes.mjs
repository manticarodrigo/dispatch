import { PrismaClient } from "@prisma/client";

const prisma = new PrismaClient();

const user = await prisma.user.findUnique({
  where: { email: "rodrigo@ambito.app" },
  include: {
    organization: {
      include: {
        places: {
          include: {
            agent: true,
          },
        },
        agents: {
          include: {
            locations: {
              orderBy: {
                createdAt: "asc",
              },
            },
          },
        },
      },
    },
  },
});

const places = user.organization.places;

// group places by created day
const places_by_day = places.reduce((acc, place) => {
  const date = place.createdAt.toISOString().slice(0, 10);
  if (!acc[date]) {
    acc[date] = [];
  }
  acc[date].push(place);
  return acc;
}, {});

// group places by day by agent
const places_by_day_by_agent = Object.entries(places_by_day).map(
  ([date, places]) => [
    new Date(date).toLocaleDateString("es-AR", {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    }),
    places.reduce((acc, place) => {
      const agent = place.agent;
      if (!agent) {
        return acc;
      }
      if (!acc[agent.name]) {
        acc[agent.name] = [];
      }
      acc[agent.name].push(place.name);
      return acc;
    }, {}),
  ]
);

function haversine(lat1, lon1, lat2, lon2) {
  function toRad(degree) {
    return (degree * Math.PI) / 180;
  }

  const R = 6371; // Earth's radius in kilometers
  const dLat = toRad(lat2 - lat1);
  const dLon = toRad(lon2 - lon1);
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(toRad(lat1)) *
      Math.cos(toRad(lat2)) *
      Math.sin(dLon / 2) *
      Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

  return R * c;
}

function totalDistanceTraveled(locations) {
  let totalDistance = 0;

  for (let i = 0; i < locations.length - 1; i++) {
    const loc1 = locations[i];
    const loc2 = locations[i + 1];

    const distance = haversine(
      loc1.latitude,
      loc1.longitude,
      loc2.latitude,
      loc2.longitude
    );
    totalDistance += distance;
  }

  return totalDistance;
}

// const places_by_day_by_agent_with_distance = places_by_day_by_agent.map(
//   ([date, places_by_agent]) => [
//     date,
//     Object.entries(places_by_agent).map(([agent_name, places]) => [
//       agent_name,
//       places,
//       user.organization.agents
//         .find((agent) => {
//           // console.log(agent);
//           return agent.name === agent_name;
//         })
//         .locations.filter(
//           (location) =>
//             new Date(location.createdAt).toLocaleDateString("es-AR", {
//               weekday: "long",
//               year: "numeric",
//               month: "long",
//               day: "numeric",
//             }) === date
//         )
//         .map((location) => {
//           // console.log(location);
//           return location.position;
//         }).length,
//     ]),
//   ]
// );

const agent_locations_by_day = user.organization.agents.map((agent) => {
  const locations_by_day = agent.locations.reduce((acc, location) => {
    const date = location.createdAt.toISOString().slice(0, 10);
    if (!acc[date]) {
      acc[date] = [];
    }
    acc[date].push(location);
    return acc;
  }, {});

  return [
    agent.name,
    Object.entries(locations_by_day).map(([date, locations]) => [
      new Date(date).toLocaleDateString("es-AR", {
        weekday: "long",
        year: "numeric",
        month: "long",
        day: "numeric",
      }),
      locations.length,
      totalDistanceTraveled(locations.map((location) => location.position)),
    ]),
  ];
});

console.log(JSON.stringify(agent_locations_by_day));
