% SCRIPT calcola_min_max_aggre
%
% For each class, this script computes the minimum and maximum values 
% of the different QoS metrics for normalizing the cost function. 

Aminmax=A(1:nrigheQoS,:);
Bminmax=B(1:nrigheQoS,:);

for indice=1:4    
    costo=zeros(nvar,1);
	for k=1:K  
        if indice==1 % time
            ind=find(xcode==ind_to_code([N+1 0 k 1],base));
            costo(ind)=costo(ind)+1*gamma(k)/s_gamma;                     
        else
            for n=1:N
                if ~node(n).tipo
                    for c=1:length(node(n).cc)
                        ind=find(xcode==ind_to_code([n c k 0],base)); 
                        if(indice==2)
                            costo(ind)=costo(ind)+node(n).lambda(k)/s_gamma*q(node(n).cc(c),indice);
                        elseif(indice==3)
                            app=-node(n).lambda(k)/gamma(k)*q(node(n).cc(c),indice);
                            app=app*gamma(k)/la_medio(k)*gamma(k)/s_gamma;
                            costo(ind)=costo(ind)+app;
                        else
                            costo(ind)=costo(ind)-node(n).lambda(k)/s_gamma*q(node(n).cc(c),indice);    
                        end
                    end
                end
            end                 
        end
        X=linprog(costo,Aminmax,Bminmax,Aeq,Beq,zeros(nvar,1),[]);   
        
        Qminmax(indice)=calcola_indice(X,N,xcode,base,q,k,indice,gamma,la_medio,0,[],[]);
        
	end
    QQminmax(indice)=(gamma*QoS(:,indice))/s_gamma;
end

