insert into public.nexus(id,player__id,level,raw_resources_available,energy_gatherer_type,material_processing_type)
 values(DEFAULT,?,?,?,?,?) returning id;