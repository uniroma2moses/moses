% SCRIPT calcola_costi_aggre_red
%
% this script computes the cost vector 'costo' of the optimization problem  


costo=zeros(nvar,1);

for indice=[1 2 4]    
  for k=1:K   
      if gamma(k)
        if indice==1 % time
            ind=find(xcode==ind_to_code([N+1 0 k 1],base));
            costo(ind)=costo(ind)+w(indice)*gamma(k)/s_gamma;         
        else
            for n=1:N
                if ~node(n).tipo
                    for J=1:length(node(n).sets)
                        ind=find(xcode==ind_to_code([n J k 0],base));     
                        if(indice==2)
                            costo(ind)=costo(ind)+node(n).lambda(k)/gamma(k)*node(n).setc(J)*...
                                       w(indice)*gamma(k)/s_gamma;
                        else
                            costo(ind)=costo(ind)-node(n).lambda(k)/gamma(k)*node(n).setr(J)*...
                                       w(indice)*gamma(k)/s_gamma;
                        end
                        
                    end
                    for J1=1:length(node(n).sets1)
                        ind=find(xcode==ind_to_code([n J1 k 4],base));     
                        if(indice==2)
                            costo(ind)=costo(ind)+node(n).lambda(k)/gamma(k)*node(n).setc1(J1)*...
                                       w(indice)*gamma(k)/s_gamma;
                        else
                            costo(ind)=costo(ind)-node(n).lambda(k)/gamma(k)*node(n).setr1(J1)*...
                                       w(indice)*gamma(k)/s_gamma;
                        end         
                    end                                        
                end
            end                 
        end      
      end  
  end
end