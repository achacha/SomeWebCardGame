insert into public.nexus(id,player__id,level,energy_gatherer_type,material_processing_type)
 values(DEFAULT,?,?,?,?) returning id;