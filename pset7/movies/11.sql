select m.title from ratings as r
left outer join movies as m
on r.movie_id = m.id
where r.movie_id in
(select movie_id from stars where person_id =
(select id from people where name = "Chadwick Boseman"))
order by r.rating desc  limit 5;
