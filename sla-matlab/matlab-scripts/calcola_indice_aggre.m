function y=calcola_indice_aggre(X,N,xcode,base,q,k,indice,gamma,la_medio,normalizzato,QQminmax,Qminmax);

global node;

y=0;

costo=zeros(1,size(xcode,1));

if indice==1 % tempo
    ind=find(xcode==ind_to_code([N+1 0 k 1],base));
    costo(ind)=1;
else
    for n=1:N
        if ~node(n).tipo
                
            for J=1:length(node(n).sets)
                ind=find(xcode==ind_to_code([n J k 0],base));     
                if(indice==2)
                    costo(ind)=node(n).lambda(k)/gamma(k)*node(n).setc(J);
                               
                elseif indice==4
                    costo(ind)=-node(n).lambda(k)/gamma(k)*node(n).setr(J);
                end
                
            end
            for J1=1:length(node(n).sets1)
                ind=find(xcode==ind_to_code([n J1 k 4],base));     
                if(indice==2)
                    costo(ind)=node(n).lambda(k)/gamma(k)*node(n).setc1(J1);
                               
                elseif indice==4
                    costo(ind)=-node(n).lambda(k)/gamma(k)*node(n).setr1(J1);
                end         
            end                                        
        end
    end                 
end

y=sum(costo*X);

if indice==3 | indice==4
    y=-y;
end
