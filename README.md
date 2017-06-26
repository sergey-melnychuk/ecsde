Event Count System Design Exercise
---

#### Overview

Stateless Scala web-app connected to Cassandra. 

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

#### Run basic queries

```
cqlsh:space> UPDATE events SET impressions = impressions + 1 WHERE user_id = 'a' and hour_tag = 416229;
cqlsh:space> UPDATE events SET impressions = impressions + 1 WHERE user_id = 'a' and hour_tag = 416229;
cqlsh:space> UPDATE events SET clicks = clicks + 1 WHERE user_id = 'b' and hour_tag = 416229;
cqlsh:space> UPDATE events SET clicks = clicks + 1 WHERE user_id = 'b' and hour_tag = 416229;
cqlsh:space> 
cqlsh:space> SELECT count(user_id) as users, sum(clicks) as clicks, sum(impressions) as impressions FROM events WHERE hour_tag = 416229;

 users | clicks | impressions
-------+--------+-------------
     2 |      2 |           2

(1 rows)

```

#### Run basic performance test

```
$ ab -n 100000 -c 100 -p zero.txt "http://127.0.0.1:8080/analytics/impression?timestamp=1498421559696&user=aaa"
...
Requests per second:    1014.60 [#/sec] (mean)
...
Percentage of the requests served within a certain time (ms)
  50%     97
...
  99%    179
 100%    282 (longest request)
```

#### Request analytics report

```
$ curl -X GET localhost:8080/analytics?timestamp=1498421559696
unqiue_users,6
clicks,200002
impressions,900002
```
