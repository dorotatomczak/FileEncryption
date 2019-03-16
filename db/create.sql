use BskDB
go

create table [User](
	UserId int identity(1,1) not null,
	Login varchar(30) not null,
	Password varchar(40) not null
)
go