select name from people where id in
(select person_id from stars where movie_id in
(SELECT id FROM  movies
WHERE year =  2004))
order by birth;
