create SEQUENCE sample_seq start with 1;
alter table sample alter column pid type varchar(20);
alter table sample alter column flag drop not null;
alter table sample alter column wdate drop not null;
alter table sample alter column rdate drop not null;
ALTER TABLE orders drop CONSTRAINT fk_order_tab_key;
ALTER TABLE orders ADD COLUMN sample_id integer;
ALTER TABLE  orders ADD CONSTRAINT order_to_sample_fk FOREIGN KEY (sample_id) REFERENCES sample (tab_key);
create SEQUENCE orders_seq start with 1;
alter table sample alter column lab_id drop not null;
alter table sample drop constraint sample_card_num_un;
alter table orders alter column flag drop not null;
alter table sample drop constraint sample_pid_un;
