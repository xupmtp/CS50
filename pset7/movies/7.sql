select m.title, r.rating
from movies as m
join ratings as r
on m.id = r.movie_id
where m.year = 2010
order by r.rating desc, m.title;