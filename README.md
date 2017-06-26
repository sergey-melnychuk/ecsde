Event Count System Design Exercise
---

#### Overview

Stateless Scala web-app connected to Cassandra, this should allow linear scalability.

#### Schema

```
CREATE TABLE events (
    user_id varchar,
    hour_tag bigint,
    impressions counter,
    clicks counter,
    PRIMARY KEY (hour_tag, user_id)
) WITH CLUSTERING ORDER BY (user_id ASC);

```

#### Run basic performance test

```
ab -n 100000 -c 100 -p zero.txt "http://127.0.0.1:8080/analytics/impression?timestamp=1498421559696&user=aaa"
```

#### Request analytics report

```
curl -X GET localhost:8080/analytics?timestamp=1498421559696
```
