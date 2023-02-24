create table users (
  user_id text not null
  , name text not null
  , created_at timestamp with time zone default CURRENT_TIMESTAMP not null
  , constraint users_PKC primary key (user_id)
) ;

create table images (
  image_id serial not null
  , user_id text not null
  , is_used boolean not null
  , create_at timestamp with time zone default CURRENT_TIMESTAMP not null
  , constraint images_PKC primary key (image_id)
) ;

alter table images
  add constraint images_FK1 foreign key (user_id) references users(user_id)
  on delete cascade;