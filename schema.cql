CREATE TABLE events (
    user_id varchar,
    hour_tag bigint,
    impressions counter,
    clicks counter,
    PRIMARY KEY (hour_tag, user_id)
);

UPDATE events SET impressions = impressions + 1 WHERE user_id = 'a' and hour_tag = 416228;
UPDATE events SET impressions = impressions + 1 WHERE user_id = 'a' and hour_tag = 416228;
UPDATE events SET clicks = clicks + 1 WHERE user_id = 'b' and hour_tag = 416228;
UPDATE events SET clicks = clicks + 1 WHERE user_id = 'b' and hour_tag = 416228;

SELECT count(user_id) as users, sum(clicks) as clicks, sum(impressions) as impressions FROM events WHERE hour_tag = 416228;
